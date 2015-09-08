Cross site scripting (or XSS) is #2 on the 2010 OWASP Top Ten web security risks and it pops up nearly everywhere.

XSS occurs when a user-manipulatable value is displayed on a web page without escaping it, allowing someone to inject Javascript or HTML into the page.

In Rails 2.x, values need to be explicitly escaped (e.g., by using the h method). In Rails 3.x, auto-escaping in views is enabled by default. However, one can still use the raw method to output a value directly.

[SOURCE](http://brakemanscanner.org/docs/warning_types/)