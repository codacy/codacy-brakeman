module SecuritySubcategory
    Mapping = {
        "Auth" => [
            "BasicAuth",
            "BasicAuthTimingAttack",
            "FilterSkipping",
            "SessionManipulation"
        ],
        "XSS" => [
            "MailTo",
            "I18nXSS",
            "LinkTo",
            "TranslateBug",
            "CrossSiteScripting",
            "StripTags",
            "JSONEncoding",
            "SelectTag",
            "SimpleFormat",
            "RenderInline"
        ],
        "InputValidation" => [
            "Deserialize",
            "JSONParsing",
            "SanitizeMethods",
            "Send",
            "ValidationRegex",
            "JRubyXML",
            "Redirect",
            "SingleQuotes",
            "UnsafeReflection",
            "Evaluation",
            "NestedAttributes",
            "ModelSerialize",
            "ModelAttributes",
            "CreateWith",
            "SafeBufferManipulation",
            "Execute",
            "YAMLParsing"
        ],
        "FileAccess" => [
            "FileDisclosure",
            "Render",
            "FileAccess"
        ],
        "HTTP" => [
            "ResponseSplitting"
        ],
        "Cookies" => [
            "SessionSettings"
        ],
        "MassAssignment" => [
            "MassAssignment",
            "WithoutProtection",
            "LinkToHref",
            "ModelAttrAccessible"
        ],
        "CSRF" => [
            "SkipBeforeFilter",
            "ForgerySetting"
        ],
        "DoS" => [
            "DigestDoS",
            "SymbolDoS",
            "RenderDoS",
            "XMLDoS",
            "SymbolDoSCVE",
            "HeaderDoS",
            "RouteDoS",
            "MimeTypeDoS"
        ],
        "SQLInjection" => [
            "SQLCVEs",
            "QuoteTableName",
            "SQL",
            "SelectVulnerability"
        ],
        "Routes" => [
            "DefaultRoutes"
        ],
        "Regex" => [
            "RegexDoS",
            "EscapeFunction"
        ],
        "SSL" => [
            "SSLVerify"
        ],
        "Cryptography" => [
            "WeakHash"
        ],
        "UnexpectedBehaviour" => [
            "SendFile"
        ],
        "Other" => [
            "DetailedExceptions",
            "UnscopedFind",
            "ContentTag"
        ]
    }
end
