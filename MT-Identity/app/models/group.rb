class Group < ActiveRecord::Base
  attr_accessible :archived, :approved, :college_year_id, :description, :project_id, :title, :user_ids, :content, :client_ids
  has_and_belongs_to_many :users, :uniq => true
  belongs_to :project
  belongs_to :college_year
  has_many :pictures, :as => :imageable
  has_and_belongs_to_many :clients

  validates :title, :presence => true
  validates :description, :presence => true
  validates :content, :presence => true


end
