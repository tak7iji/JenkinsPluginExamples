class HelloworldProxy < Jenkins::Tasks::BuilderProxy
  proxy_for ::HelloworldBuilder

  def initialize plugin, object
    super
  end

  def doHello
    p "Hello World"
  end

  def call a
    @object.call a
  end
end
