require "brakeman"
require "brakeman/scanner"
require "brakeman/report/report_codeclimate"
require "json"

checks = Brakeman::Checks.checks

r = checks.map { |check|
  {
    :patternId => check.name.sub!("Check", ""),
    :level => "Warning",
    :category => "Security",
  }
}

patterns = {
  :name => "brakeman",
  :version => Brakeman::Version,
  :patterns => r.sort_by { |pattern| pattern[:patternId] },
}

File.open("../src/main/resources/docs/patterns.json", "w") do |f|
  f.puts JSON.pretty_generate(patterns)
end

puts YAML.load_file(Brakeman::Report::CodeClimate::REMEDIATION_POINTS_CONFIG_PATH)

description = checks.map { |check|
  {
    :patternId => check.name,
    :title => check.description,
    :description => check.description,
    :timeToFix => 20,
  }
}.sort_by { |pattern| pattern[:patternId] }

File.open("../src/main/resources/docs/description/description.json", "w") do |f|
  f.puts JSON.pretty_generate(description)
end

puts Brakeman::WarningCodes::Codes
