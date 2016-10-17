class CreatePictures < ActiveRecord::Migration
  def change
    create_table :pictures do |t|
      t.string :description
      t.string :image
      t.integer :crop_x
      t.integer :crop_y
      t.integer :crop_w
      t.integer :crop_h
      t.references :imageable, :polymorphic => true

      t.timestamps
    end
  end
end