include  Java

java_import Java.hudson.BulkChange
java_import Java.hudson.model.listeners.SaveableListener

class HelloworldDescriptor < Jenkins::Model::DefaultDescriptor
  include Jenkins::Model::Descriptor

  attr_accessor :gname

  def initialize *a
    super
    load
  end

  java_signature 'public FormValidation doCheckName(@QueryParameter String value)'
  def doCheckName value
    p value
    Java.FormValidation.ok
  end

  def configure req, json
    parse json
    save
    true
  end

  def parse form
    @gname = form["gname"]
  end
  
  def save
    return if BulkChange.contains self

    begin
      File.open(configFile.file.canonicalPath, 'wb') do |f|
        f.write(to_xml)
      end
      SaveableListener.fireOnChange(self, configFile)
    rescue IOError => e
      p e.message
    end
  end

  def load
    return unless configFile.file.exists
    from_xml(File.read(configFile.file.canonicalPath))
  end

  def to_xml
"<?xml version='1.0' encoding='UTF-8'?>
 <#{id} plugin='hellowolrdbuilder'>
   <gname>#{@gname}</gname>
 </#{id}>"
  end

  def from_xml xml
    @gname = xml.scan(/<gname>(.*)<\/gname>/).flatten.first
  end     
end
