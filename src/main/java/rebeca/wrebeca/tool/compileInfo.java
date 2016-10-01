package rebeca.wrebeca.tool;

public class compileInfo {
    
        private static final compileInfo instance=new compileInfo();
        private compileInfo(){
            reduction=false;
            //public static boolean with_tau=false;
            compile=false;
            queue=true;
            //public static boolean wRebeca = false;
            bag=false; //true=>bag,false=>queue
            clts=false;
            lts = true;
            mcrl=false;
            dynamic=true ;
            max_thread_num=4;
        }
        
        public static compileInfo getInstance(){
            return instance;
        }

        public  boolean reduction;

    public boolean isReduction() {
        return reduction;
    }

    public void setReduction(boolean reduction) {
        this.reduction = reduction;
    }

    public boolean isCompile() {
        return compile;
    }

    public void setCompile(boolean compile) {
        this.compile = compile;
    }

    public boolean isQueue() {
        return queue;
    }

    public void setQueue(boolean queue) {
        this.queue = queue;
    }

    public boolean isBag() {
        return bag;
    }

    public void setBag(boolean bag) {
        this.bag = bag;
    }

    public boolean isClts() {
        return clts;
    }

    public void setClts(boolean clts) {
        this.clts = clts;
    }

    public boolean isLts() {
        return lts;
    }

    public void setLts(boolean lts) {
        this.lts = lts;
    }

    public boolean isMcrl() {
        return mcrl;
    }

    public void setMcrl(boolean mcrl) {
        this.mcrl = mcrl;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public int getMax_thread_num() {
        return max_thread_num;
    }

    public void setMax_thread_num(int max_thread_num) {
        this.max_thread_num = max_thread_num;
    }
        //public static boolean with_tau=false;
        private  boolean compile;
        private  boolean queue;
        //public static boolean wRebeca = false;
        private  boolean bag; //true=>bag,false=>queue
        private  boolean clts;
        private  boolean lts;
        private  boolean mcrl;
        private  boolean dynamic;
        private  int max_thread_num;
        
}
