require "brakeman"
require "brakeman/scanner"
require "brakeman/report/report_codeclimate"
require "json"

checks = Brakeman::Checks.checks
remediations = YAML.load_file(Brakeman::Report::CodeClimate::REMEDIATION_POINTS_CONFIG_PATH)
directory = "vendor/bundle/ruby/2.3.0/gems/brakeman-#{Brakeman::Version}/lib/brakeman/checks/*.rb"
mapping = Brakeman::WarningCodes::Codes.collect { |code, value|
  next if code == :csrf_protection_disabled ||
          code == :CVE_2012_2660 ||
          code == :CVE_2012_2661 ||
          code == :CVE_2012_2695 ||
          code == :CVE_2012_5664 ||
          code == :CVE_2013_0155 ||
          code == :CVE_2013_6417 ||
          code == :CVE_2014_3482 ||
          code == :CVE_2014_3483 ||
          code == :CVE_2015_7578 ||
          code == :CVE_2015_7580 ||
          code == :CVE_2016_6317 ||
          code == :CVE_2018_3741
  file = Dir[directory].find { |file| File.read(file).include?(":#{code},") } ||
         Dir[directory].find { |file| File.read(file).include?(code.to_s) }
  check = checks
    .sort_by { |check| -check.name.size }
    .find { |check| File.basename(file, ".*").tr("_", "").include?(check.name.downcase) }
  {
    :code => code.to_s,
    :patternId => check.name.sub("Check", ""),
    :value => value,
    :remediation => [(remediations[code.to_s] || 100000) * 20 / 100000, 4 * 60].min,
  }
}.compact.sort_by { |pattern| pattern[:value] }

patterns_with_category = checks.map { |check|
  {
    :patternId => check.name.sub("Check", ""),
    :level => "Warning",
    :category => "Security",
  }
}

patterns = {
  :name => "brakeman",
  :version => Brakeman::Version,
  :patterns => patterns_with_category.sort_by { |pattern| pattern[:patternId] },
}

File.open("../src/main/resources/docs/patterns.json", "w") do |f|
  f.puts JSON.pretty_generate(patterns)
end

description = checks.map { |check|
  {
    :patternId => check.name.sub("Check", ""),
    :title => check.description,
    :description => check.description,
    :timeToFix => 20,
  }
}.sort_by { |pattern| pattern[:patternId] }

File.open("../src/main/resources/docs/description/description.json", "w") do |f|
  f.puts JSON.pretty_generate(description)
end

puts mapping.map { |pattern| "      case #{pattern[:value]} => \"#{pattern[:patternId]}\"" }
