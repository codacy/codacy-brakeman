# Ruby 3+/4 compatibility shim for Brakeman 4.3.1 (written for Ruby 2.5-2.7).
# Loaded via RUBYOPT before Brakeman; does not modify Brakeman itself.

# Ruby 3.0: Array subclass methods return Array instead of the subclass.
# Brakeman's Sexp < Array depends on the old behaviour.
module ArraySubclassCompat
  %i[[] slice slice! drop drop_while take take_while flatten uniq *].each do |m|
    define_method(m) do |*args, &blk|
      result = super(*args, &blk)
      klass = self.class
      klass != Array && result.instance_of?(Array) ? klass.allocate.replace(result) : result
    end
  end
end
Array.prepend(ArraySubclassCompat)

# Ruby 3.2: removed aliases / taint API used by old bundled gems.
class << File; alias_method :exists?, :exist? unless method_defined?(:exists?); end
class << Dir;  alias_method :exists?, :exist? unless method_defined?(:exists?); end
class Object
  def taint; self; end unless method_defined?(:taint)
  def untaint; self; end unless method_defined?(:untaint)
  def tainted?; false; end unless method_defined?(:tainted?)
end
