There is a class of vulnerabilities known as Unsafe Reflection related to the recent deserialization vulnerabilities found in Rails.
Unsafe Reflection vulnerabilities via constant creation occur in Ruby normally when the Module#const_get method is called with user-controlled data.

[Source](http://blog.conviso.com.br/2013/02/exploiting-unsafe-reflection-in.html)
