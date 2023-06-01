package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;


//    public HashMap<String, Artist> artistMap;   // added

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;


    /*added*/ public int mostsonglikes = Integer.MIN_VALUE;
    public int mostartistlikes = Integer.MIN_VALUE;
    public Song mostlikedSong;
    public Artist mostlikedArtist;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

//        artistMap = new HashMap<>();  // added

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();




    }

    public User createUser(String name, String mobile) {
        User user = new User(name, mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        artistAlbumMap.put(artist, new ArrayList<>());
//        artistMap.put(name, artist);  // added
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Album album = new Album(title);
        albums.add(album);

        albumSongMap.put(album, new ArrayList<>());
        Artist atst = new Artist() ;
        Boolean found = false;


        for(Artist artist: artists){
            if(artist.getName().equals(artistName)){
                atst = artist;
                List oldlist = artistAlbumMap.get(atst);
                oldlist.add(album);
                artistAlbumMap.put(atst, oldlist);
                found = true;
                break;
            }
        }
        if(found == false){
            atst = new Artist(artistName);
            List newlist = new ArrayList();
            newlist.add(album);
            artistAlbumMap.put(atst, newlist);
        }

        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        boolean found = false;
        Album alb = new Album();
        for(Album album : albums){
            if(album.getTitle().equals(albumName)){
                alb = album;
                found = true;
            }
        }
        if(!found) throw new Exception("Album does not exist");

        Song song = new Song(title, length);
        List<Song> oldlist =  albumSongMap.get(alb);
        oldlist.add(song);
        albumSongMap.put(alb, oldlist);
        songs.add(song);
        songLikeMap.put(song, new ArrayList<User>());
        return song ;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {

        boolean found = false;
        User usr = new User();
        for(User user : users){
            if(user.getMobile().equals(mobile)){
                usr = user;
                found = true;
                break;
            }
        }
        if(!found)throw new Exception("User does not exist");

        List<Song> lst = new ArrayList<>();
        for(Song song : songs){
            if(song.getLength() == length){
                lst.add(song);
            }
        }
        Playlist playlist = new Playlist(title);
        playlists.add(playlist);

        playlistSongMap.put(playlist, lst);


        List<User> unewlst = new ArrayList<>();
        unewlst.add(usr);
        playlistListenerMap.put(playlist, unewlst);

        creatorPlaylistMap.put(usr, playlist);

        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        boolean found = false;
        User usr = new User();
        for(User user : users){
            if(user.getMobile().equals(mobile)){
                usr = user;
                found = true;
                break;
            }
        }
        if(!found)throw new Exception("User does not exist");

        List<Song> lst = new ArrayList<>();
        for(String sTitle :songTitles){
            for(Song song : songs){
                if(song.getTitle().equals(sTitle)){
                    lst.add(song);
                }
            }
        }

        Playlist playlist = new Playlist(title);
        playlists.add(playlist);

        playlistSongMap.put(playlist, lst);


        List<User> unewlst = new ArrayList<>();
        unewlst.add(usr);
        playlistListenerMap.put(playlist, unewlst);

        creatorPlaylistMap.put(usr, playlist);

        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        // FIND USER AND IF USER DOES NOT EXIST THROW ERROR
        boolean found = false;
        User usr = new User();
        for(User user : users){
            if(user.getMobile().equals(mobile)){
                usr = user;
                found = true;
                break;
            }
        }
        if(!found)throw new Exception("User does not exist");


        // FIND PLAYLIST AND IF it DOES NOT EXIST THROW ERROR
        boolean found2 = false;
        Playlist plst = new Playlist();
        for(Playlist playlist : playlists){
            if(playlist.getTitle().equals(playlistTitle)){
                plst = playlist;
                found2 = true;
                break;
            }
        }
        if(!found2)throw new Exception("Playlist does not exist");


        // NOW WE HAVE BOTH USER AND PLAYLIST
        List<User> playlstusers = playlistListenerMap.get(plst);
        for(User u : playlstusers){
            if(u.getMobile().equals(usr.getMobile()))
                return plst;
        }

        playlstusers.add(usr);
        playlistListenerMap.put(plst, playlstusers);
        List<Playlist>uplsts = userPlaylistMap.get(usr);
        uplsts.add(plst);
        userPlaylistMap.put(usr, uplsts);
        return plst;

    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        boolean found = false;
        User usr = new User();
        for(User user : users){
            if(user.getMobile().equals(mobile)){
                usr = user;
                found = true;
                break;
            }
        }
        if(!found)throw new Exception("User does not exist");


        boolean found2 = false;
        Song sng = new Song();
        for(Song song : songs){
            if(song.getTitle().equals(songTitle)){
                sng = song;
                found2 = true;
                break;
            }
        }
        if(!found2)throw new Exception("Song does not exist");


        List<User> usrlst =  songLikeMap.get(sng);
        boolean usrfound = false;
        for(User u : usrlst){
            if(u.getMobile().equals(usr.getMobile())){
                usrfound = true;
                break;
            }
        }

        if(!usrfound){
            usrlst.add(usr);
            songLikeMap.put(sng, usrlst);
            /*added*/  if(mostsonglikes<songLikeMap.get(sng).size()){
                mostsonglikes = songLikeMap.get(sng).size();
                mostlikedSong = sng;
            }

            // Bruteforce

            //FIND ALBUM BY SONG from albumsongmap
            Album albm = new Album();
            for(Album abm : albumSongMap.keySet()){
                for(Song s: albumSongMap.get(abm)){
                    if(s.getTitle().equals(songTitle)){
                        albm = abm;
                        break;
                    }
                }
            }
            Artist atst  = new Artist();
            for(Artist ast : artistAlbumMap.keySet()){
                for(Album a: artistAlbumMap.get(ast)){
                    if(a.getTitle().equals(albm.getTitle())){
                        atst = ast;
                        break;
                    }
                }
            }
            atst.setLikes(atst.getLikes()+1);
            if(mostartistlikes < atst.getLikes()) {
                mostartistlikes = atst.getLikes();
                mostlikedArtist = atst;
            }
        }

        return sng;


    }

    public String mostPopularArtist() {
        return mostlikedArtist.getName();
    }

    public String mostPopularSong() {
        return mostlikedSong.getTitle();
    }
}
