package model;

/**
 * Created by jason on 7/20/14.
 */
@Ann
public class Impl implements API {
    int myField = -1;

    @Ann
    String implMethod() {
        return "@@@@@";
    }

    @Override
    public void someApiMethod() {

    }
}
