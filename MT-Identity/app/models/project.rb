class Project < ActiveRecord::Base
  attr_accessible :title, :description, :school_year, :preview, :remove_preview, :highlight, :college_year_ids, :client_ids
  has_and_belongs_to_many :college_years
  has_many :groups
  has_one :sticker

  mount_uploader :preview, BannerUploader

  validates :school_year, :presence => true

  YEARS = ["", "1e jaar", "2e jaar", "3e jaar", "4e jaar"]
  def school_year_display
    YEARS[school_year]
  end

end
