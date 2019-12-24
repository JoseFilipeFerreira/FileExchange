# Media Provider server

## Request format

Requests will follow the format `{type='$TYPE', content=['$CONTENT1','$CONTENT2',...]}`,
where requests with matching types can be done all at one. At this moment it only suports
one request at the time.

Request replies will follow the format 
```{status='$STATUS', result='$RESULT'}``` 
where `$STATUS` can be both `ok` or `failed`

At the moment the server support requests:

### Adding users

Adding users to the system can be done with the request below, replacing $NAME with the username
and $PASSWD with the corresponding password

```{type='add_user', content=['User{name='$NAME':passwd='$PASSWD'}',]}```

### User login

The login request is very similar to the user adding request

```{type='login', content=['User{name='$NAME':passwd='$PASSWD'}',]}```

### Uploading Music

Uploading music to the server can be done with the request 

```{type='add_music', content=['Music{title='$TITLE':artist='$ARTIST':year=$YEAR:tags=['$TAG1','$TAG2',]}',]}```

by replacing the music title, artist, year and list of tags in the correspondent place

### Searching

Searching by a tag can be achived with the request  

```{type='search', content=['$TAG',]}```

by substituting $TAG by the desired tag
