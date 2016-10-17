class Client < ActiveRecord::Base
  attr_accessible :name, :description, :site
  has_and_belongs_to_many :groups
end
