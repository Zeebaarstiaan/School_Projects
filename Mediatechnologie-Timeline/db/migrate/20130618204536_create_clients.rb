class CreateClients < ActiveRecord::Migration
  def change
    create_table :clients do |t|
      t.string :name
      t.text :description
      t.string :site

      t.timestamps
    end

    create_table :clients_groups, :id => false do |t|
      t.references :client, :group
    end

    add_index :clients_groups, [:client_id, :group_id]
  end
end
