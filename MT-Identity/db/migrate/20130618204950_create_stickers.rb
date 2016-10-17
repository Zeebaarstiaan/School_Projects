class CreateStickers < ActiveRecord::Migration
  def change
    create_table :stickers do |t|
      t.string :title
      t.text :description
      t.string :link
      t.integer :project_id

      t.timestamps
    end
  end
end
