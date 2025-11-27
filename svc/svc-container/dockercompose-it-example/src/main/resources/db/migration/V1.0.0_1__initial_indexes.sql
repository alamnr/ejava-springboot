drop index IF EXISTS idx_song_title;

create index idx_song_title on reposongs_song(title);