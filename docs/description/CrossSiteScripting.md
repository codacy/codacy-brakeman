XSS occurs when a user-manipulatable value is displayed on a web page without escaping it, allowing someone to inject Javascript or HTML into the page.

In Rails 2.x, values need to be explicitly escaped (e.g., by using the h method). In Rails 3.x, auto-escaping in views is enabled by default. However, one can still use the raw method to output a value directly.


[Source](http://brakemanscanner.org/docs/warning_types/cross_site_scripting/)
