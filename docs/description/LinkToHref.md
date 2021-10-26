Warns for calls to link_to which pass in potentially hazardous data
to the second argument.  While this argument must be html_safe to not break
the html, it must also be url safe as determined by calling a
:url_safe_method.  This prevents attacks such as javascript:evil() or
data:<encoded XSS> which is html_safe, but not safe as an href

[Source](http://brakemanscanner.org/docs/warning_types/)
