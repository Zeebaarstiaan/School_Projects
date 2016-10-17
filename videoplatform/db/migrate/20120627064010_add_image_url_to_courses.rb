class AddImageUrlToCourses < ActiveRecord::Migration
  def change
    add_column :courses, :image_url, :string, default: "course_icons/no_icon.png"
  end
end
