class CreateUsers < ActiveRecord::Migration
  def change
    create_table :users do |t|
      t.string :name
      t.integer :login_code
      t.string :password_digest
      t.string :remember_token

      t.timestamps
    end

    add_column :users, :account_type, :integer, default: 0
  end
end
