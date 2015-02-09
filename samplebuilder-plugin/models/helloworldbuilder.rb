# require 'jenkins/rack'
require_relative 'helloworlddescriptor'

class HelloworldBuilder < Jenkins::Tasks::Builder
    display_name "Helloworld builder"
    attr_accessor :name

    include Jenkins::Model
    include Jenkins::Model::DescribableNative
#     include Jenkins::RackSupport

    describe_as Java.hudson.tasks.Builder, :with => HelloworldDescriptor

    # Invoked with the form parameters when this extension point
    # is created from a configuration screen.
    def initialize(attrs = {})
      @name = attrs['name']
    end

    ##
    # Runs before the build begins
    #
    # @param [Jenkins::Model::Build] build the build which will begin
    # @param [Jenkins::Model::Listener] listener the listener for this build.
    def prebuild(build, listener)
      # do any setup that needs to be done before this build runs.
    end

    ##
    # Runs the step over the given build and reports the progress to the listener.
    #
    # @param [Jenkins::Model::Build] build on which to run this step
    # @param [Jenkins::Launcher] launcher the launcher that can run code on the node running this build
    # @param [Jenkins::Model::Listener] listener the listener for this build.
    def perform(build, launcher, listener)
      # actually perform the build step
      p Jenkins.plugin.descriptors[HelloworldBuilder].gname
      listener << "Hello, #{@name}"
      launcher.execute "echo","Hello, #{@name}", :out => listener
    end

    def call a
#      a.each do |key, value|
#        p key,value
#      end
      [200, {"Content-Type" => "text/plain"}, ["Hello world!"]]
    end

    def method_missing name
      p name
    end

    # Jenkins::Plugin::Proxies.register self, ::HelloworldProxy
end
#Jenkins::Plugin.instance.register_extension(HelloworldBuilder)
