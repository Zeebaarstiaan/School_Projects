require 'test_helper'

class UserTest < ActiveSupport::TestCase
  test "close users" do
    current_user = User.create!(
      name:      "Current User",
      email:      "current@example.com",
      password: "password",
      lonlat:   'POINT(-76.000000 39.000000)'
    )

    far_user = User.create!(
      name:      "Far User",
      email:      "far@example.com",
      password: "password",
      lonlat:   'POINT(-77.000000 40.000000)'
    )

    close_user = User.create!(
      name:      "Close User",
      email:      "close@example.com",
      password: "password",
      lonlat:   'POINT(-75.990000 39.010000)'
    )

    close_users = User.close_to(current_user, 2000)
    # close_users = User.find_by_sql("SELECT * FROM users WHERE ST_DWithin(lonlat, '#{current_user.lonlat}', 2000) AND NOT id = #{current_user.id} ")

    assert_equal 1,          close_users.size
    assert_equal close_user, close_users.first
  end

end
