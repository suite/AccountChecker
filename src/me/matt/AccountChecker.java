package me.matt;

/**
 * Created by Matt on 2/16/17.
 */
public class AccountChecker {

    public static void main(String [ ] args) {

        if(args.length<2) {
            System.out.println("3");
            return;
        }

        String username = args[0];
        String password = args[1];

        CheckAccount checkAccount = new CheckAccount(username, password);
        checkAccount.validate();

    }


}
