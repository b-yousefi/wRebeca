package wRebeca.common;

    class stMcrl_number
    {
        public String mcrl_state;
        public int num_messages;
        public stMcrl_number(String str, int num)
        {
            mcrl_state = str;
            num_messages = num;
        }

        public Boolean last_message(int num)
        {
            if (num_messages == num)
                return true;
            else
                return false;
        }

    }
