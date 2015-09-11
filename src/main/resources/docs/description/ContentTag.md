Warns for unescaped values in `content_tag`

    content_tag :tag, body
                       ^-- Unescaped in Rails 2.x

    content_tag, :tag, body, attribute => value
                                ^-- Unescaped in all versions

    content_tag, :tag, body, attribute => value
                                            ^
                                            |
            Escaped by default, can be explicitly escaped
            or not by passing in (true|false) as fourth argument

[Source](http://brakemanscanner.org/docs/warning_types/)
