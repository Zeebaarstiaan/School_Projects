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

require 'spec_helper'

describe Video do
  pending "add some examples to (or delete) #{__FILE__}"
end
