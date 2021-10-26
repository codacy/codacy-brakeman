Also known as information leakage or information exposure, this vulnerability refers to system or internal information (such as debugging output, stack traces, error messages, etc.) which is displayed to an end user.

For example, Rails provides detailed exception reports by default in the development environment, but it is turned off by default in production:

    # Full error reports are disabled
    config.consider_all_requests_local = false

This settings should not be true in production, and neither should be a show_detailed_exceptions? method in a controller which does not return false.

[Source](http://brakemanscanner.org/docs/warning_types/information_disclosure/)
