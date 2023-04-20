#!/bin/bash
#
#   Copyright 2023, Ray Elenteny
#
#   Permission is hereby granted, free of charge, to any person obtaining a copy
#   of this software and associated documentation files (the "Software"), to deal
#   in the Software without restriction, including without limitation the rights
#   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
#   copies of the Software, and to permit persons to whom the Software is
#   furnished to do so, subject to the following conditions:
#
#   The above copyright notice and this permission notice shall be included in
#   all copies or substantial portions of the Software.
#
#   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
#   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
#   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
#   THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
#   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
#   FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
#   DEALINGS IN THE SOFTWARE.
#
#

source ./functions.sh

development_deployment="false"

function usage() {
  echo " "
  echo "Usage:"
  echo "   -d deploy development environment"
}

while getopts "d" opt
do
  case $opt in
    d) development_deployment="true";;
    *) usage;;
  esac
done


build_chart_version
chart_options="-f yaml/local.yaml"
if [ "${development_deployment}" == "true" ]; then
    chart_options="-f yaml/development.yaml ${chart_options}"
fi

helm repo update
helm upgrade --install ${helm_deployment_name} -n ${helm_namespace} ${helm_repository_name}/${helm_chart_name} \
             --version ${helm_chart_version} --create-namespace ${chart_options}
