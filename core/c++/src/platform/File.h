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

#ifndef SRC_PLATFORM_FILE_H_
#define SRC_PLATFORM_FILE_H_

#include <forward_list>
#include <string>

#include <dirent.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>
#include <iostream>
#include <fstream>

using namespace std;

class FilenameFilter {
	virtual bool accept(File& dir, string& name) = 0;
	virtual ~FilenameFilter() {
	}
};

class File {
	std::string path;

public:
	static const char separatorChar = '/';

	File(std::string path /* to either a directory or a file */) {
		this->path = path;
	}

	bool isDirectory() {
		struct stat st;
		if (stat(path.c_str(), &st) == 0) {
			if (S_ISDIR(st.st_mode)) {
				return true;
			}
		}
		return false;
	}

	bool canWrite() {
		struct stat st;
		if (stat(path.c_str(), &st) == 0) {
			if (st.st_mode & S_IFDIR != 0) {
				return true;
			}
		}
		return false;
	}

	string getName() {
		std::string base_filename;
		std::string::size_type const o(path.find_last_of("/\\"));
		if (o != std::string::npos) {
			base_filename = path.substr(o + 1);
		}

		std::string::size_type const p(base_filename.find_last_of('.'));
		if (p != std::string::npos) {
			std::string file_without_extension = base_filename.substr(0, p);
			return file_without_extension;
		} else {
			return base_filename;
		}
	}

	void listFiles(forward_list<File *>& list, FilenameFilter& filter) {
		DIR *dpdf;
		struct dirent *epdf;
		dpdf = opendir(path.c_str());
		if (dpdf != NULL) {
			while (epdf = readdir(dpdf)) {
				printf("Filename: %s", epdf->d_name);
				string fname(epdf->d_name);
				if (filter.accept(*this, fname)) {
					File *p_new_file = new File(epdf->d_name);
					list.insert_after(list.end(), p_new_file);
				}
			}
			closedir(dpdf);
		}
	}

};

class PrintWriter {
	ofstream out;
public:

	PrintWriter(const string& file_path) :
			out(file_path.c_str()) {
	}

	void println(const string& line) {
		if (out) {
			out << line << "\n";
		}
	}
	void close() {
		if (out) {
			out.close();
		}
	}

	~PrintWriter() {
		close();
	}
};

#endif /* SRC_PLATFORM_FILE_H_ */
