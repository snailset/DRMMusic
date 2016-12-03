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

public class Album {
    public final long artistId;      // 1
    public final String artistName;  // 薛之谦
    public final long id;            // 1
    public final int songCount;      // 1
    public final String title;       // 绅士
    public final int year;           // 0

    public Album() {
        this.id = -1;
        this.title = "";
        this.artistName = "";
        this.artistId = -1;
        this.songCount = -1;
        this.year = -1;
    }

    public Album(long _id, String _title, String _artistName, long _artistId, int _songCount, int _year) {
        this.id = _id;
        this.title = _title;
        this.artistName = _artistName;
        this.artistId = _artistId;
        this.songCount = _songCount;
        this.year = _year;
    }

    @Override
    public String toString() {
        return "Album{" +
                "artistId=" + artistId +
                ", artistName='" + artistName + '\'' +
                ", id=" + id +
                ", songCount=" + songCount +
                ", title='" + title + '\'' +
                ", year=" + year +
                '}';
    }
}
