#!/bin/bash

source ./variables.sh

function execute_chartmuseum_delete() {
  local chart_name_len=${#helm_chart_name}
  if [[ "${line}" != "-" ]]; then
    local chart_file=$(echo "${line}" | cut -d'/' -f2)
    chart_file="${chart_file%.*}"
    local version=${chart_file:chart_name_len+1}
    curl -XDELETE ${helm_repository_url}/api/charts/${helm_chart_name}/${version} >/dev/null
  fi
}

function delete_chart_from_chartmuseum() {
  local semver=${helm_chart_semver}
  for line in $(curl -s ${helm_repository_url}/index.yaml | grep "charts/${helm_chart_name}-${semver}+"); do
    execute_chartmuseum_delete
  done

  for line in $(curl -s ${helm_repository_url}/index.yaml | grep "charts/${helm_chart_name}-${semver}-"); do
    execute_chartmuseum_delete
  done
}

function build_chart_version() {
  local version_part=$(echo "${helm_application_version}" | cut -d'-' -f1)
  local major_part=$(echo "${version_part}" | cut -d'.' -f1)
  local minor_part=$(echo "${version_part}" | cut -d'.' -f2)
  local patch_part=$(echo "${version_part}" | cut -d'.' -f3)
  local build_part=$(echo "${version_part}" | cut -d'.' -f4)

  helm_chart_semver="${major_part}.${minor_part}.${patch_part}"
  helm_chart_version="${helm_chart_semver}-${build_part}+${helm_application_version}"
}

function copy_ssh() {
  if [[ ! -d "${HOME}/.ssh" ]]; then
    echo " "
    echo "*********************"
    echo "${HOME}/.ssh does not exist"
    echo "*********************"
    echo " "
    exit 1
  fi
  mkdir -p ${chart_directory}/media-application/ssh
  cp ${HOME}/.ssh/* ${chart_directory}/media-application/ssh
}

function publish_chart() {
  local parent_chart="$1"

  build_chart_version
  echo "Chart version: ${helm_chart_version}"

  if [[ "${helm_package_development}" == "true" ]]; then
    copy_ssh
  fi

  if [[ "${deployment_type}" == "local" ]]; then
    delete_chart_from_chartmuseum "${helm_chart_name}"
  fi

  status_code=$(curl -n -s -w "%{http_code}" ${helm_repository_url}/index.yaml -o /dev/null)
  if [[ "${status_code}" == "200" ]]; then
    cd "${chart_directory}" || exit
    sed -i".bak" "s/version: \${helm.chart.version}/version: ${helm_chart_version}/" ${helm_chart_name}/Chart.yaml
    rm ${helm_chart_name}/Chart.yaml.bak

    packaged_chart_file="${helm_chart_name}-${helm_chart_version}.tgz"
    if [[ -n "${parent_chart}" ]]; then
      helm dependency update ${helm_chart_name}
    fi
    helm package ${helm_chart_name}

    ls

    if [[ "${deployment_type}" == "local" ]]; then
      curl --data-binary "@${packaged_chart_file}" ${helm_repository_url}/api/charts >/dev/null
    else
      curl -n -X PUT ${helm_repository_url}/${packaged_chart_file} -T "${packaged_chart_file}" >/dev/null
    fi
  else
    echo " "
    echo "Helm chart repository not reachable. Charts will not be pushed to a repository."
    echo " "
  fi
}
