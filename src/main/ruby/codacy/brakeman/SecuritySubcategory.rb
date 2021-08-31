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
            "RenderInline",
            "JSONEntityEscape",
            "ReverseTabnabbing"
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
            "YAMLParsing",
            "PageCachingCVE",
            "TemplateInjection"
        ],
        "FileAccess" => [
            "FileDisclosure",
            "Render",
            "FileAccess",
            "SprocketsPathTraversal"
        ],
        "HTTP" => [
            "ResponseSplitting"
        ],
        "Cookies" => [
            "SessionSettings",
            "CookieSerialization"
        ],
        "MassAssignment" => [
            "MassAssignment",
            "WithoutProtection",
            "LinkToHref",
            "ModelAttrAccessible"
        ],
        "CSRF" => [
            "SkipBeforeFilter",
            "ForgerySetting",
            "CSRFTokenForgeryCVE"
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
            "SSLVerify",
            "ForceSSL"
        ],
        "Cryptography" => [
            "WeakHash"
        ],
        "UnexpectedBehaviour" => [
            "SendFile",
            "VerbConfusion"
        ],
        "Other" => [
            "DetailedExceptions",
            "UnscopedFind",
            "ContentTag"
        ],
        "CommandInjection" => [
            "UnsafeReflectionMethods"
        ]
    }
end
