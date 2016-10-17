# == Schema Information
#
# Table name: ratings
#
#  created_at :datetime         not null
#  id         :integer          not null, primary key
#  updated_at :datetime         not null
#  user_id    :integer
#  value      :integer
#  video_id   :integer
#

class Rating < ActiveRecord::Base
  attr_accessible :value

  belongs_to :user
  belongs_to :video
end
