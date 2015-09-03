Warn about denial of service with render :text (CVE-2014-0082)

actionpack/lib/action_view/template/text.rb in Action View in Ruby on Rails 3.x before 3.2.17 converts MIME type strings to symbols during use of the :text option to the render method,
which allows remote attackers to cause a denial of service (memory consumption) by including these strings in headers.

[SOURCE](https://groups.google.com/d/msg/rubyonrails-security/LMxO_3_eCuc/ozGBEhKaJbIJ)