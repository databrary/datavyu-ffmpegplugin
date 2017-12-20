#include "LogicForBuffer.hpp"

template<class Item>
class ItemBuffer : public LogicForBuffer<Item> {
public:
    ItemBuffer(long firstItem, int nItem, int nMaxReverse) : LogicForBuffer<Item>(firstItem, nItem, nMaxReverse) {
        buffer = new Item[nItem];
    }
    virtual ~ItemBuffer() {
        delete [] buffer;
    }
};