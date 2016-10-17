class CreateVideos < ActiveRecord::Migration
  def change
    create_table :videos do |t|
      t.string :title
      t.string :url
      t.float :rating
      t.text :description
      t.integer :course_id

      t.timestamps
    end
  end
end
