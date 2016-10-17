class CollegeYear < ActiveRecord::Base
  attr_accessible :start_year
  has_and_belongs_to_many :projects

  validates :start_year, :uniqueness => true

  def years
    "#{start_year} - #{start_year+1}"
  end
end
