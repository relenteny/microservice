/*
 * Copyright 2020, Ray Elenteny
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

create table if not exists media.audio
(
    id           varchar(255) not null
        constraint audio_pkey
            primary key,
    title        varchar(255),
    album        varchar(255),
    album_artist varchar(255),
    artist       varchar(255),
    duration     bigint,
    track_number integer,
    year         integer
);

alter table media.audio
    owner to "media-service";

create table if not exists media.movies
(
    id              varchar(255) not null
        constraint movies_pkey
            primary key,
    title           varchar(255),
    audience_rating double precision,
    content_rating  varchar(255),
    critics_rating  double precision,
    directors       varchar(255),
    duration        bigint,
    genres          varchar(255),
    released        date,
    roles           varchar(255),
    studio          varchar(255),
    summary         varchar(2048),
    tagline         varchar(255),
    year            integer
);

alter table media.movies
    owner to "media-service";

create table if not exists media.tv_shows
(
    id               varchar(255) not null
        constraint tv_shows_pkey
            primary key,
    title            varchar(255),
    content_rating   varchar(255),
    directors        varchar(255),
    duration         bigint,
    episode          integer,
    originally_aired date,
    rating           double precision,
    season           integer,
    series_title     varchar(255),
    studio           varchar(255),
    summary          varchar(8192),
    writers          varchar(255),
    year             integer
);

alter table media.tv_shows
    owner to "media-service";
