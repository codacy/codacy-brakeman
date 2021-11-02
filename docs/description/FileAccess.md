Using user input when accessing files (local or remote) will raise a warning.

For example

    File.open("/tmp/#{cookie[:file]}")
    will raise an error like

    Cookie value used in file name near line 4: File.open("/tmp/#{cookie[:file]}")

This type of vulnerability can be used to access arbitrary files on a server (including /etc/passwd.

[Source](http://brakemanscanner.org/docs/warning_types/file_access/)
