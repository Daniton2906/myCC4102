package priority_queue;

import box.*;

public class BoxFactory {

    public Box create(PriorityQueue cp) {
        return cp.create(this);
    }

    protected HCBox createHCBox() {
        return new HCBox();
    }

    protected CBBox createCBBox() {
        return new CBBox();
    }

    protected CFBox createCFBox() {
        return new CFBox();
    }

    protected LHBox createLHBox() {
        return new LHBox();
    }

    protected SHBox createSHBox() {
        return new SHBox();
    }
}
