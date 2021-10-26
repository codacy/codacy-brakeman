##Patterns: ModelSerialize,ModelAttributes
##Issue: {"severity": "Warn", "line": 3, "patternId": "ModelSerialize"}
class Product < ActiveRecord::Base
  serialize :price
  ##Warn: ModelAttributes
  attr_protected :price
end
