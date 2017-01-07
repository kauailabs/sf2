/* ============================================
 SF2 source code is placed under the MIT license
 Copyright (c) 2017 Kauai Labs

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 ===============================================
 */

#ifndef SRC_QUANTITY_IQUANTITY_H_
#define SRC_QUANTITY_IQUANTITY_H_

#include <forward_list>
#include <string>

using namespace std;

class IQuantity {
public:
	/* Returns true if this quantity has a printable value */
	virtual bool getPrintableString(vector<string>& printable_string) = 0;
	/* Returns true if this is a quantity container */
	virtual bool getContainedQuantities(
			vector<IQuantity *>& quantities) = 0;
	/* Returns true if this is a quantity container. */
	virtual bool getContainedQuantityNames(
			vector<string>& quantity_names) = 0;
	virtual ~IQuantity() {
	}
};

#endif /* SRC_QUANTITY_IQUANTITY_H_ */
