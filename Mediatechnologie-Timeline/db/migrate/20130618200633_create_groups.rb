class CreateGroups < ActiveRecord::Migration
  def change
    create_table :groups do |t|
      t.string :title
      t.text :description
      t.text :content
      t.integer :project_id
      t.integer :college_year_id
      t.boolean :archived, :default => false
      t.boolean :approved, :default => false

      t.timestamps
    end

    create_table :groups_users, :id => false do |t|
      t.references :group, :user
    end

    add_index :groups_users, [:group_id, :user_id]
  end
end
