require 'ffaker'

task :users_init => :environment do
  make_users
end

def make_users
  p "Create Sample Users"
  50.times do |n|
    p "- Create user #{n+1}"
    lon = -76 - Random.rand() * 10
    lat = 39 + Random.rand() * 10
    User.create do |u|
      u.name = Faker::Name.name
      u.email = "testuser#{n+1}@example.com"
      u.password = "password"
      u.password_confirmation = "password"
      u.lonlat = "POINT(#{lon} #{lat})"
    end
  end
end