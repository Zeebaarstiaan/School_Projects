require 'ffaker'

namespace :db do
  desc "Fill database with sample data"
  task :populate => :environment do
    make_users
    make_clients
    make_projects
    make_college_years
  end
  task :initialize => :environment do
    p 'Reset database'
    reset_db
    p 'Clear previous Carrierwave uploads'
    FileUtils.rm_rf(Dir.glob('public/uploads/*'))
    Rake::Task['db:populate'].invoke
  end
  task :users_init => :environment do
    p 'Reset database'
    reset_db
    p 'Clear previous Carrierwave uploads'
    FileUtils.rm_rf(Dir.glob('public/uploads/*'))
    make_users
  end
end

def reset_db
  Rake::Task['db:drop'].invoke
  Rake::Task['db:create'].invoke
  Rake::Task['db:migrate'].invoke
end

def make_users
  @admin = User.create do |u|
      u.first_name = "Admin"
      u.last_name = "User"
      u.email = "admin@hr.nl"
      u.password = "password"
      u.password_confirmation = "password"
      u.student_number = "0836947"
      u.starting_year = Date.new(2009+rand(5),1,1)
      u.portfolio = Faker::Internet.domain_name
      u.description = 'De baas van dit hele zaakje'
      u.avatar = File.open(File.join(Rails.root, "app/assets/images/demo/users/0.jpg"))
      u.admin = true
    end
    p "Create Admin: #{@admin.first_name} #{@admin.last_name}"

  p "Create Sample Users"
  10.times do |n|
    p "- Create user #{n+1}"
    User.create do |u|
      u.first_name = Faker::Name.first_name
      u.last_name = Faker::Name.last_name
      u.email = "user#{n+1}@hr.nl"
      u.password = "password"
      u.password_confirmation = "password"
      u.student_number = "0"+(836950+n).to_s
      u.starting_year = Date.new(2009+rand(5),1,1)
      u.portfolio = Faker::Internet.domain_name
      u.description = Faker::Lorem.sentence(5)
      u.avatar =File.open(File.join(Rails.root, "app/assets/images/demo/users/#{n+1}.jpg"))
    end
  end
end

def make_college_years
  p "Create College years"
  projects = Project.all
  @college_year1  = CollegeYear.create do |c|
    c.start_year = 2013
    c.projects = projects[0..4]
  end
  p "- College year: #{@college_year1.years}"
  p "-- Projects: #{@college_year1.projects.map{ |p| p.title}.join(", ")}"
  @college_year2  = CollegeYear.create do |c|
    c.start_year = 2014
    c.projects = projects[4..8]
  end
  p "- College year: #{@college_year2.years}"
  p "-- Projects: #{@college_year2.projects.map{ |p| p.title}.join(", ")}"

  projects.each do |p|
    p.groups.each do |g|
      p "Link #{g.title} to #{p.college_years.first.years}"
      g.college_year_id = p.college_years.first.id
      g.save
    end
  end
end

def make_projects
  p "Create 9 sample projects"
  9.times do |i|
    random = rand(26-1) + 1
    p "- Create sample project #{i+1}"
    @project = Project.create do |p|
      p.title = "Sample Project #{i+1}"
      p.description = Faker::Lorem.paragraph(paragraph_count = 5)
      p.school_year = 1
      p.preview = File.open(File.join(Rails.root, "app/assets/images/demo/cards/#{random}.jpg"))
    end
    3.times do |t|
      users = User.all
      p "-- Create sample team #{t+1}"
      @group = Group.create do |g|
        g.title = "Sample Team #{t+1}"
        g.description = Faker::Lorem.paragraph(paragraph_count = 5)
        g.content = Faker::Lorem.paragraph(paragraph_count = 15)
        g.users = users[(t+1)..(t+3)]
        g.project = @project
      end
      3.times do |n|
        p "--- Add Image"
        random = rand(26-1) + 1
        pi = Picture.create!(:imageable_id => @group.id, :imageable_type => "Group")
        pi.image.store!(File.open(File.join(Rails.root, "app/assets/images/demo/cards/#{random}.jpg")))
        @group.pictures << pi
        @group.save!
      end
    end
  end
end

def make_clients
  p "Create sample clients"
  5.times do |n|
    Client.create do |c|
      c.name = "Sample Client #{n+1}"
      c.description = Faker::Lorem.paragraph(paragraph_count = 5)
      c.site = Faker::Internet.domain_name
    end
  end
end
