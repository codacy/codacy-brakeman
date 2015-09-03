Unscoped find (and related methods) are a form of Direct Object Reference. Models which belong to another model should typically be accessed via a scoped query.

For example, if an Account belongs to a User, then this may be an unsafe unscoped find:

    Account.find(params[:id])

Depending on the action, this could allow an attacker to access any account they wish.

Instead, it should be scoped to the currently logged-in user:

    current_user = User.find(session[:user_id])
    current_user.accounts.find(params[:id])

[SOURCE](http://brakemanscanner.org/docs/warning_types/unscoped_find/)