# == Schema Information
#
# Table name: courses
#
#  created_at :datetime         not null
#  id         :integer          not null, primary key
#  name       :string(255)
#  updated_at :datetime         not null
#

class Course < ActiveRecord::Base
  attr_accessible :name, :image_url
  has_many :videos

  validates :name, presence: true, length: { maximum: 50 }
  validates :image_url, presence: true
  
end
