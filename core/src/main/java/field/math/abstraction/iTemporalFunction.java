package field.math.abstraction;

public
interface ITemporalFunction<T> {
    public
    T get(float alpha);

    public
    float getDomainMin();

    public
    float getDomainMax();
}
