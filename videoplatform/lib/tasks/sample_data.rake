namespace :db do
  desc "Fill database with sample data"
  task populate: :environment do
    make_users
    make_courses
  end
end

def make_users
  User.create!(name: "Jan",
               surname: "Appel",
               login_code: "1234",
               account_type: "2",
               password: "test123",
               password_confirmation: "test123")
  User.create!(name: "Peter",
               surname: "Perenboom",
               login_code: "123",
               account_type: "1",
               password: "test123",
               password_confirmation: "test123")
  20.times do |n|
    name  = Faker::Name.first_name
    surname = Faker::Name.last_name
    login_code = "#{n+1}"
    password  = "test123"
    User.create!(name: name,
                 surname: surname,
                 login_code: login_code,
                 password: password,
                 password_confirmation: password)
  end
end

def make_videos
  20.times do |n|
    title  = (Faker::Lorem.words(2)).join(" ")
    description = Faker::Lorem.paragraphs(2).join("\n")
    urls = ["http://www.youtube.com/watch?v=nyK9fp-nmwA&feature=plcp","http://www.youtube.com/watch?v=7z3v8Ur36nA&feature=plcp",
      "http://www.youtube.com/watch?v=9Rk2iAJS2TE&feature=plcp", "http://www.youtube.com/watch?v=CAOEwhxqgmg&feature=plcp", "http://www.youtube.com/watch?v=WDxaYf0N62k&feature=plcp"]
    url = urls[(rand(0..4)).to_i]
    course_id = (rand(1..6)).to_i;
    Video.create!(title: title,
                 description: description,
                 url: url,
                 course_id: course_id,
                 )
  end
end

def make_courses
  Course.create!(name: "Biologie",
               image_url: "course_icons/biologie.png")
  Course.create!(name: "Aardrijkskunde",
               image_url: "course_icons/aardrijkskunde.png")
  Course.create!(name: "Engels",
               image_url: "course_icons/engels.png")
  Course.create!(name: "Geschiedenis",
               image_url: "course_icons/geschiedenis.png")
  Course.create!(name: "Rekenen",
               image_url: "course_icons/rekenen.png")
  Course.create!(name: "Nederlands",
               image_url: "course_icons/nederlands.png")
  Course.create!(name: "Muziek",
               image_url: "course_icons/no_icon.png")
end