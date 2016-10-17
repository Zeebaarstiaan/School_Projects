# == Schema Information
#
# Table name: videos
#
#  course_id   :integer
#  created_at  :datetime         not null
#  description :text
#  id          :integer          not null, primary key
#  rating      :float
#  title       :string(255)
#  updated_at  :datetime         not null
#  url         :string(255)
#

class Video < ActiveRecord::Base
  attr_accessible :course_id, :description, :rating, :title, :url, :course_id
  has_many :ratings
  has_many :raters, :through => :ratings, :source => :users
  belongs_to :course
  has_many :views
  has_many :users, :through => :views

  validates :url, presence: true
  validates :title, presence: true, length: { maximum: 50 }
  validates :description, presence: true
  validates :course_id, presence: true

  def average_rating
    @value = 0
    self.ratings.each do |rating|
        @value = @value + rating.value
    end
    @total = self.ratings.size
    self.rating = (@value.to_f / @total.to_f).round(2)
  end
end
