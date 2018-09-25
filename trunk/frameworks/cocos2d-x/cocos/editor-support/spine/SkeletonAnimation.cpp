/******************************************************************************
 * Spine Runtimes Software License v2.5
 *
 * Copyright (c) 2013-2016, Esoteric Software
 * All rights reserved.
 *
 * You are granted a perpetual, non-exclusive, non-sublicensable, and
 * non-transferable license to use, install, execute, and perform the Spine
 * Runtimes software and derivative works solely for personal or internal
 * use. Without the written permission of Esoteric Software (see Section 2 of
 * the Spine Software License Agreement), you may not (a) modify, translate,
 * adapt, or develop new applications using the Spine Runtimes or otherwise
 * create derivative works or improvements of the Spine Runtimes or (b) remove,
 * delete, alter, or obscure any trademarks or any copyright, trademark, patent,
 * or other intellectual property or proprietary rights notices on or in the
 * Software, including any copy thereof. Redistributions in binary or source
 * form must include this license and terms.
 *
 * THIS SOFTWARE IS PROVIDED BY ESOTERIC SOFTWARE "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL ESOTERIC SOFTWARE BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES, BUSINESS INTERRUPTION, OR LOSS OF
 * USE, DATA, OR PROFITS) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *****************************************************************************/

#include <spine/SkeletonAnimation.h>
#include <spine/spine-cocos2dx.h>
#include <spine/extension.h>
#include <algorithm>

USING_NS_CC;
using std::min;
using std::max;
using std::vector;

namespace spine {

typedef struct _TrackEntryListeners {
    StartListener startListener;
    InterruptListener interruptListener;
    EndListener endListener;
    DisposeListener disposeListener;
    CompleteListener completeListener;
    EventListener eventListener;
} _TrackEntryListeners;
    
void animationCallback (spAnimationState* state, spEventType type, spTrackEntry* entry, spEvent* event) {
	((SkeletonAnimation*)state->rendererObject)->onAnimationStateEvent(entry, type, event);
}

void trackEntryCallback (spAnimationState* state, spEventType type, spTrackEntry* entry, spEvent* event) {
	((SkeletonAnimation*)state->rendererObject)->onTrackEntryEvent(entry, type, event);
    if (type == SP_ANIMATION_DISPOSE)
        if (entry->rendererObject) delete (spine::_TrackEntryListeners*)entry->rendererObject;
}

static _TrackEntryListeners* getListeners (spTrackEntry* entry) {
	if (!entry->rendererObject) {
		entry->rendererObject = new spine::_TrackEntryListeners();
		entry->listener = trackEntryCallback;
	}
	return (_TrackEntryListeners*)entry->rendererObject;
}
    
//

SkeletonAnimation* SkeletonAnimation::createWithData (spSkeletonData* skeletonData, bool ownsSkeletonData) {
	SkeletonAnimation* node = new SkeletonAnimation();
	node->initWithData(skeletonData, ownsSkeletonData);
	node->autorelease();
	return node;
}

SkeletonAnimation* SkeletonAnimation::createWithJsonFile (const std::string& skeletonJsonFile, spAtlas* atlas, float scale) {
	SkeletonAnimation* node = new SkeletonAnimation();
	node->initWithJsonFile(skeletonJsonFile, atlas, scale);
	node->autorelease();
	return node;
}

SkeletonAnimation* SkeletonAnimation::createWithJsonFile (const std::string& skeletonJsonFile, const std::string& atlasFile, float scale) {
	SkeletonAnimation* node = new SkeletonAnimation();
	spAtlas* atlas = spAtlas_createFromFile(atlasFile.c_str(), 0);
	node->initWithJsonFile(skeletonJsonFile, atlas, scale);
	node->autorelease();
	return node;
}

SkeletonAnimation* SkeletonAnimation::createWithBinaryFile (const std::string& skeletonBinaryFile, spAtlas* atlas, float scale) {
	SkeletonAnimation* node = new SkeletonAnimation();
	node->initWithBinaryFile(skeletonBinaryFile, atlas, scale);
	node->autorelease();
	return node;
}

SkeletonAnimation* SkeletonAnimation::createWithBinaryFile (const std::string& skeletonBinaryFile, const std::string& atlasFile, float scale) {
	SkeletonAnimation* node = new SkeletonAnimation();
	spAtlas* atlas = spAtlas_createFromFile(atlasFile.c_str(), 0);
	node->initWithBinaryFile(skeletonBinaryFile, atlas, scale);
	node->autorelease();
	return node;
}


void SkeletonAnimation::initialize () {
	super::initialize();

	_ownsAnimationStateData = true;
	_state = spAnimationState_create(spAnimationStateData_create(_skeleton->data));
	_state->rendererObject = this;
	_state->listener = animationCallback;

	_spAnimationState* stateInternal = (_spAnimationState*)_state;
}

SkeletonAnimation::SkeletonAnimation ()
		: SkeletonRenderer() {
}

SkeletonAnimation::~SkeletonAnimation () {
	if (_ownsAnimationStateData) spAnimationStateData_dispose(_state->data);
	spAnimationState_dispose(_state);
}

void SkeletonAnimation::update (float deltaTime) {
	super::update(deltaTime);

	deltaTime *= _timeScale;
	spAnimationState_update(_state, deltaTime);
	spAnimationState_apply(_state, _skeleton);
	spSkeleton_updateWorldTransform(_skeleton);
}

void SkeletonAnimation::setAnimationStateData (spAnimationStateData* stateData) {
	CCASSERT(stateData, "stateData cannot be null.");

    if (_ownsAnimationStateData) spAnimationStateData_dispose(_state->data);
    spAnimationState_dispose(_state);

	_ownsAnimationStateData = false;
	_state = spAnimationState_create(stateData);
	_state->rendererObject = this;
	_state->listener = animationCallback;
}

void SkeletonAnimation::setMix (const std::string& fromAnimation, const std::string& toAnimation, float duration) {
	spAnimationStateData_setMixByName(_state->data, fromAnimation.c_str(), toAnimation.c_str(), duration);
}

spTrackEntry* SkeletonAnimation::setAnimation (int trackIndex, const std::string& name, bool loop) {
	spAnimation* animation = spSkeletonData_findAnimation(_skeleton->data, name.c_str());
	if (!animation) {
		log("Spine: Animation not found: %s", name.c_str());
		return 0;
	}
	return spAnimationState_setAnimation(_state, trackIndex, animation, loop);
}

spTrackEntry* SkeletonAnimation::addAnimation (int trackIndex, const std::string& name, bool loop, float delay) {
	spAnimation* animation = spSkeletonData_findAnimation(_skeleton->data, name.c_str());
	if (!animation) {
		log("Spine: Animation not found: %s", name.c_str());
		return 0;
	}
	return spAnimationState_addAnimation(_state, trackIndex, animation, loop, delay);
}
	
spTrackEntry* SkeletonAnimation::setEmptyAnimation (int trackIndex, float mixDuration) {
	return spAnimationState_setEmptyAnimation(_state, trackIndex, mixDuration);
}

void SkeletonAnimation::setEmptyAnimations (float mixDuration) {
	spAnimationState_setEmptyAnimations(_state, mixDuration);
}

spTrackEntry* SkeletonAnimation::addEmptyAnimation (int trackIndex, float mixDuration, float delay) {
	return spAnimationState_addEmptyAnimation(_state, trackIndex, mixDuration, delay);
}

spAnimation* SkeletonAnimation::findAnimation(const std::string& name) const {
	return spSkeletonData_findAnimation(_skeleton->data, name.c_str());
}

spTrackEntry* SkeletonAnimation::getCurrent (int trackIndex) { 
	return spAnimationState_getCurrent(_state, trackIndex);
}

void SkeletonAnimation::clearTracks () {
	spAnimationState_clearTracks(_state);
}

void SkeletonAnimation::clearTrack (int trackIndex) {
	spAnimationState_clearTrack(_state, trackIndex);
}

void SkeletonAnimation::onAnimationStateEvent (spTrackEntry* entry, spEventType type, spEvent* event) {
	switch (type) {
	case SP_ANIMATION_START:
		if (_startListener) _startListener(entry);
		break;
    case SP_ANIMATION_INTERRUPT:
        if (_interruptListener) _interruptListener(entry);
        break;
	case SP_ANIMATION_END:
		if (_endListener) _endListener(entry);
		break;
    case SP_ANIMATION_DISPOSE:
        if (_disposeListener) _disposeListener(entry);
        break;
	case SP_ANIMATION_COMPLETE:
		if (_completeListener) _completeListener(entry);
		break;
	case SP_ANIMATION_EVENT:
		if (_eventListener) _eventListener(entry, event);
		break;
	}
}

void SkeletonAnimation::onTrackEntryEvent (spTrackEntry* entry, spEventType type, spEvent* event) {
	if (!entry->rendererObject) return;
	_TrackEntryListeners* listeners = (_TrackEntryListeners*)entry->rendererObject;
	switch (type) {
	case SP_ANIMATION_START:
		if (listeners->startListener) listeners->startListener(entry);
		break;
    case SP_ANIMATION_INTERRUPT:
        if (listeners->interruptListener) listeners->interruptListener(entry);
        break;
	case SP_ANIMATION_END:
		if (listeners->endListener) listeners->endListener(entry);
		break;
    case SP_ANIMATION_DISPOSE:
        if (listeners->disposeListener) listeners->disposeListener(entry);
        break;
	case SP_ANIMATION_COMPLETE:
		if (listeners->completeListener) listeners->completeListener(entry);
		break;
	case SP_ANIMATION_EVENT:
		if (listeners->eventListener) listeners->eventListener(entry, event);
		break;
	}
}

void SkeletonAnimation::setStartListener (const StartListener& listener) {
	_startListener = listener;
}
    
void SkeletonAnimation::setInterruptListener (const InterruptListener& listener) {
    _interruptListener = listener;
}
    
void SkeletonAnimation::setEndListener (const EndListener& listener) {
	_endListener = listener;
}
    
void SkeletonAnimation::setDisposeListener (const DisposeListener& listener) {
    _disposeListener = listener;
}

void SkeletonAnimation::setCompleteListener (const CompleteListener& listener) {
	_completeListener = listener;
}

void SkeletonAnimation::setEventListener (const EventListener& listener) {
	_eventListener = listener;
}

void SkeletonAnimation::setTrackStartListener (spTrackEntry* entry, const StartListener& listener) {
	getListeners(entry)->startListener = listener;
}
    
void SkeletonAnimation::setTrackInterruptListener (spTrackEntry* entry, const InterruptListener& listener) {
    getListeners(entry)->interruptListener = listener;
}

void SkeletonAnimation::setTrackEndListener (spTrackEntry* entry, const EndListener& listener) {
	getListeners(entry)->endListener = listener;
}
    
void SkeletonAnimation::setTrackDisposeListener (spTrackEntry* entry, const DisposeListener& listener) {
    getListeners(entry)->disposeListener = listener;
}

void SkeletonAnimation::setTrackCompleteListener (spTrackEntry* entry, const CompleteListener& listener) {
	getListeners(entry)->completeListener = listener;
}

void SkeletonAnimation::setTrackEventListener (spTrackEntry* entry, const EventListener& listener) {
	getListeners(entry)->eventListener = listener;
}

spAnimationState* SkeletonAnimation::getState() const {
	return _state;
}

SkeletonAnimation* SkeletonAnimation::createFromCache(const std::string& skeletonDataKeyName)
{
	if (spSkeletonData* skeleton_data = getSkeletonDataFromCache(skeletonDataKeyName)){
		SkeletonAnimation* node = new SkeletonAnimation();
		node->initWithData(skeleton_data, false);
		node->autorelease();
		return node;
	}

	return nullptr;
}

spSkeletonData* SkeletonAnimation::readSkeletonDataToCache(const std::string& skeletonDataKeyName, const std::string& skeletonDataFile, const std::string& atlasFile, float scale /*= 1*/)
{
	ItSkeletonData it = _all_skeleton_data_cache.find(skeletonDataKeyName);

	if (it == _all_skeleton_data_cache.end()){
		SkeletonDataInCache skeleton_data_in_cache;
		skeleton_data_in_cache._atlas = nullptr;
		skeleton_data_in_cache._skeleton_data = nullptr;



		skeleton_data_in_cache._atlas = spAtlas_createFromFile(atlasFile.c_str(), 0);
		CCASSERT(skeleton_data_in_cache._atlas, "Error reading atlas file.");

		spAttachmentLoader* attachmentLoader = SUPER(Cocos2dAttachmentLoader_create(skeleton_data_in_cache._atlas));

		spSkeletonBinary* binary = spSkeletonBinary_createWithLoader(attachmentLoader);
		binary->scale = scale;
		skeleton_data_in_cache._skeleton_data = spSkeletonBinary_readSkeletonDataFile(binary, skeletonDataFile.c_str());
		CCASSERT(skeleton_data_in_cache._skeleton_data, binary->error ? binary->error : "Error reading skeleton data file.");
		spSkeletonBinary_dispose(binary);

		if (skeleton_data_in_cache._atlas && skeleton_data_in_cache._skeleton_data){
			_all_skeleton_data_cache[skeletonDataKeyName] = skeleton_data_in_cache;

			return skeleton_data_in_cache._skeleton_data;
		}
		else{ //错误处理，释放创建的资源
			if (skeleton_data_in_cache._skeleton_data){
				spSkeletonData_dispose(skeleton_data_in_cache._skeleton_data);
			}

			if (skeleton_data_in_cache._atlas){
				spAtlas_dispose(skeleton_data_in_cache._atlas);
			}
		}
	}

	return nullptr;
}

spSkeletonData* SkeletonAnimation::getSkeletonDataFromCache(const std::string& skeletonDataKeyName)
{
	ItSkeletonData it = _all_skeleton_data_cache.find(skeletonDataKeyName);
	if (it != _all_skeleton_data_cache.end()){
		return it->second._skeleton_data;
	}

	return nullptr;
}

bool SkeletonAnimation::removeSkeletonData(const std::string& skeletonDataKeyName)
{
	ItSkeletonData it = _all_skeleton_data_cache.find(skeletonDataKeyName);
	if (it != _all_skeleton_data_cache.end()){
		if (it->second._skeleton_data) spSkeletonData_dispose(it->second._skeleton_data);
		if (it->second._atlas) spAtlas_dispose(it->second._atlas);

		_all_skeleton_data_cache.erase(it);
		return true;
	}

	return false;
}

void SkeletonAnimation::removeAllSkeletonData()
{
	for (ItSkeletonData it = _all_skeleton_data_cache.begin(); it != _all_skeleton_data_cache.end(); ++it){
		if (it->second._skeleton_data) spSkeletonData_dispose(it->second._skeleton_data);
		if (it->second._atlas) spAtlas_dispose(it->second._atlas);
	}

	_all_skeleton_data_cache.clear();
}

bool SkeletonAnimation::isExistSkeletonDataInCache(const std::string& skeletonDataKeyName)
{
	ItSkeletonData it = _all_skeleton_data_cache.find(skeletonDataKeyName);
	if (it != _all_skeleton_data_cache.end()){
		return true;
	}

	return false;
}

std::map<std::string, SkeletonAnimation::SkeletonDataInCache> SkeletonAnimation::_all_skeleton_data_cache; //初始化静态成员


//底层修改
//struct spBone {
//	spBoneData* const data;
//	struct spSkeleton* const skeleton;
//	spBone* const parent;
//	int childrenCount;
//	spBone** const children;
//	float x, y, rotation, scaleX, scaleY, shearX, shearY;
//	float ax, ay, arotation, ascaleX, ascaleY, ashearX, ashearY;
//	int /*bool*/ appliedValid;
//
//	float const a, b, worldX;
//	float const c, d, worldY;
//
//	int/*bool*/ sorted;
//
//#ifdef __cplusplus
//	spBone() :
//		data(0),
//		skeleton(0),
//		parent(0),
//		childrenCount(0), children(0),
//		x(0), y(0), rotation(0), scaleX(0), scaleY(0),
//		ax(0), ay(0), arotation(0), ascaleX(0), ascaleY(0), ashearX(0), ashearY(0),
//		appliedValid(0),
//
//		a(0), b(0), worldX(0),
//		c(0), d(0), worldY(0),
//
//		sorted(0) {
//	}
//#endif
//};

float SkeletonAnimation::getAnimationDuration(const std::string& animationName)
{
	spAnimation *animation = spSkeletonData_findAnimation(_skeleton->data, animationName.c_str());
	if (!animation) return -1;
	return animation->duration;
}

float SkeletonAnimation::getEventTime(const std::string& animationName, const std::string& eventName)
{
	spAnimation *animation = spSkeletonData_findAnimation(_skeleton->data, animationName.c_str());
	if (!animation) return -1;
	spEventTimeline *eventTimeline = NULL;
	for (int i = 0; i<animation->timelinesCount; i++) {
		spTimeline *timeline = animation->timelines[i];
		if (timeline->type == SP_TIMELINE_EVENT) {
			eventTimeline = SUB_CAST(spEventTimeline, timeline);
			break;
		}
	}
	if (!eventTimeline) return -1;
	for (int i = 0; i<eventTimeline->framesCount; i++) {
		float time = eventTimeline->frames[i];
		spEvent *event = eventTimeline->events[i];
		const char *name = event->data->name;
		if (strcmp(name, eventName.c_str()) == 0) {
			return time;
		}
	}
	return -1;
}

cocos2d::CCNodeRGBA* SkeletonAnimation::getNodeForSlot(const char* slotName)
{

	spSlot* slot = findSlot(slotName);
	if (slot != NULL){
		CCNodeRGBA* node = new CCNodeRGBA();
		if (node->init()){
			node->autorelease();
		}
		node->setPosition(0, 0);
		this->addChild(node);

		spBone* bone = slot->bone;
		if (bone != NULL){
			node->setPosition(ccp(bone->worldX, bone->worldY));
			//node->setRotation(-bone->worldRotation);
			//node->setScaleX(bone->worldScaleX);
			//node->setScaleY(bone->worldScaleY);
			//            float flipXFactor = bone->flipX ? -1.0 : 1.0;
			//            float flipYFactor = bone->flipY ? -1.0 : 1.0;
			//            node->setScaleX(node->getScaleX()*flipXFactor);
			//            node->setScaleY(node->getScaleX()*flipYFactor);
		}
		//node->setopacity(255 * slot->a);
		//node->setcolor(ccc3(255 * slot->r, 255 * slot->g, 255 * slot->b));
		//node->setvisible(slot->attachment);

		//sSlotNode slot_node;
		//slot_node.slot = slot;
		//slot_node.node = node;
		//m_slotNodes.insert(SlotNodeMap::value_type(slotName, slot_node));
		//CCLOG("C++ getNodeForSlot %s worldX :%f  worldY :%f", slotName, node->getPositionX(), node->getPositionY());
		return node;
	}
	else{
		return NULL;
	}

	//SlotNodeIter iter = m_slotNodes.find(slotName);
	//if (iter != m_slotNodes.end()) {
	//	sSlotNode& slot_node = iter->second;
	//	return slot_node.node;
	//}
	//else{
	//	spSlot* slot = findSlot(slotName);

	//	if (slot != NULL){
	//		CCNodeRGBA* node = new CCNodeRGBA();
	//		if (node->init()){
	//			node->autorelease();
	//		}
	//		node->setPosition(0, 0);
	//		this->addChild(node);
	//		sSlotNode slot_node;
	//		slot_node.slot = slot;
	//		slot_node.node = node;
	//		m_slotNodes.insert(SlotNodeMap::value_type(slotName, slot_node));
	//		return node;
	//	}
	//	else{
	//		return NULL;
	//	}
	//}
}

//void SkeletonAnimation::draw()
//{
//	SkeletonRenderer::draw();
//
//	for (SlotNodeIter iter = m_slotNodes.begin(), end = m_slotNodes.end(); iter != end; ++iter) {
//		sSlotNode& slot_node = iter->second;
//		spSlot* slot = slot_node.slot;
//		CCNodeRGBA* node = slot_node.node;
//		spBone* bone = slot->bone;
//		if (bone != NULL){
//			node->setPosition(ccp(bone->worldX, bone->worldY));
//			node->setRotation(-bone->worldRotation);
//			node->setScaleX(bone->worldScaleX);
//			node->setScaleY(bone->worldScaleY);
//			//            float flipXFactor = bone->flipX ? -1.0 : 1.0;
//			//            float flipYFactor = bone->flipY ? -1.0 : 1.0;
//			//            node->setScaleX(node->getScaleX()*flipXFactor);
//			//            node->setScaleY(node->getScaleX()*flipYFactor);
//		}
//		node->setOpacity(255 * slot->a);
//		node->setColor(ccc3(255 * slot->r, 255 * slot->g, 255 * slot->b));
//		node->setVisible(slot->attachment);
//	}
//}

//}

}
