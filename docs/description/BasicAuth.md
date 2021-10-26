In Rails 3.1, a new feature was added to simplify basic authentication.

The example provided in the official Rails Guide looks like this:

    class PostsController < ApplicationController

      http_basic_authenticate_with :name => "dhh", :password => "secret", :except => :index

      #...

    end
    
This warning will be raised if http_basic_authenticate_with is used and the password is found to be a string (i.e., stored somewhere in the code).

[Source](http://brakemanscanner.org/docs/warning_types/basic_authentication/)
