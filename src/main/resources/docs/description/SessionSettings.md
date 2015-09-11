*HTTP Only*

It is recommended that session cookies be set to “http-only”. This helps prevent stealing of cookies via cross site scripting.

*Secret Length*

The tool will warn if the key length for the session cookies is less than 30 characters.

*Version control inclusion*

The tool will warn if the config/initializers/secret_token.rb is included in the version control. It is recommended that secret_token.rb is excluded from version control, and included in .gitignore

[Source](http://brakemanscanner.org/docs/warning_types/session_setting/)
