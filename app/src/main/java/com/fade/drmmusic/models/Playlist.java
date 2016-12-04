/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package com.fade.drmmusic.models;

public class Playlist {

    public final long id;
    public final String name;
    public final int songCount;
    public final String albumArt;
    public final String author;

    public Playlist() {
        this.id = -1;
        this.name = "";
        this.songCount = -1;
        albumArt = null;
        author = null;
    }


    public Playlist(long id, String name, int songCount, String albumArt, String author) {
        this.id = id;
        this.name = name;
        this.songCount = songCount;
        this.albumArt = albumArt;
        this.author = author;
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", songCount=" + songCount +
                '}';
    }
}
