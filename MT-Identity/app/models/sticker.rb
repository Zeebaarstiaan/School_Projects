class Sticker < ActiveRecord::Base
  attr_accessible :project_id, :description, :link, :title
  belongs_to :project
  belongs_to :college_year
end
