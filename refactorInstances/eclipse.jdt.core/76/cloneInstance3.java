					for (int j = 1; j < numberOfFrames; j++) {
						// select next frame
						currentFrame = (StackMapFrame) frames.get(j);
						// generate current frame
						// need to find differences between the current frame and the previous frame
						int frameOffset = currentFrame.pc;
						// FULL_FRAME
						if (localContentsOffset + 5 >= this.contents.length) {
							resizeContents(5);
						}
						this.contents[localContentsOffset++] = (byte) (frameOffset >> 8);
						this.contents[localContentsOffset++] = (byte) frameOffset;
						int numberOfLocalOffset = localContentsOffset;
						localContentsOffset += 2; // leave two spots for number of locals
						int numberOfLocalEntries = 0;
						int numberOfLocals = currentFrame.getNumberOfLocals();
						int numberOfEntries = 0;
						int localsLength = currentFrame.locals == null ? 0 : currentFrame.locals.length;
						for (int i = 0; i < localsLength && numberOfLocalEntries < numberOfLocals; i++) {
							if (localContentsOffset + 3 >= this.contents.length) {
								resizeContents(3);
							}
							VerificationTypeInfo info = currentFrame.locals[i];
							if (info == null) {
								this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_TOP;
							} else {
								switch(info.id()) {
									case T_boolean :
									case T_byte :
									case T_char :
									case T_int :
									case T_short :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_INTEGER;
										break;
									case T_float :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_FLOAT;
										break;
									case T_long :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_LONG;
										i++;
										break;
									case T_double :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_DOUBLE;
										i++;
										break;
									case T_null :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_NULL;
										break;
									default:
										this.contents[localContentsOffset++] = (byte) info.tag;
									switch (info.tag) {
										case VerificationTypeInfo.ITEM_UNINITIALIZED :
											int offset = info.offset;
											this.contents[localContentsOffset++] = (byte) (offset >> 8);
											this.contents[localContentsOffset++] = (byte) offset;
											break;
										case VerificationTypeInfo.ITEM_OBJECT :
											int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
											this.contents[localContentsOffset++] = (byte) (indexForType >> 8);
											this.contents[localContentsOffset++] = (byte) indexForType;
									}
								}
								numberOfLocalEntries++;
							}
							numberOfEntries++;
						}
						if (localContentsOffset + 4 >= this.contents.length) {
							resizeContents(4);
						}
						this.contents[numberOfLocalOffset++] = (byte) (numberOfEntries >> 8);
						this.contents[numberOfLocalOffset] = (byte) numberOfEntries;
						int numberOfStackItems = currentFrame.numberOfStackItems;
						this.contents[localContentsOffset++] = (byte) (numberOfStackItems >> 8);
						this.contents[localContentsOffset++] = (byte) numberOfStackItems;
						for (int i = 0; i < numberOfStackItems; i++) {
							if (localContentsOffset + 3 >= this.contents.length) {
								resizeContents(3);
							}
							VerificationTypeInfo info = currentFrame.stackItems[i];
							if (info == null) {
								this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_TOP;
							} else {
								switch(info.id()) {
									case T_boolean :
									case T_byte :
									case T_char :
									case T_int :
									case T_short :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_INTEGER;
										break;
									case T_float :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_FLOAT;
										break;
									case T_long :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_LONG;
										break;
									case T_double :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_DOUBLE;
										break;
									case T_null :
										this.contents[localContentsOffset++] = (byte) VerificationTypeInfo.ITEM_NULL;
										break;
									default:
										this.contents[localContentsOffset++] = (byte) info.tag;
									switch (info.tag) {
										case VerificationTypeInfo.ITEM_UNINITIALIZED :
											int offset = info.offset;
											this.contents[localContentsOffset++] = (byte) (offset >> 8);
											this.contents[localContentsOffset++] = (byte) offset;
											break;
										case VerificationTypeInfo.ITEM_OBJECT :
											int indexForType = this.constantPool.literalIndexForType(info.constantPoolName());
											this.contents[localContentsOffset++] = (byte) (indexForType >> 8);
											this.contents[localContentsOffset++] = (byte) indexForType;
									}
								}
							}
						}
					}
